package com.bear.core.config.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.bear.core.Event;
import com.bear.core.config.AbstractConfiguration;
import com.bear.core.config.RecorderConfig;
import com.bear.core.config.xml.object.util.ObjectBuilder;
import com.bear.core.util.Closer;



public class XmlConfiguration extends AbstractConfiguration {

  private static final long serialVersionUID = 1L;

  private static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
  private static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  private static final String[] VERBOSE_CLASSES = new String[] { XmlConfiguration.class.getName() };
  private static final String LOG4J_XSD = "Log4j-config.xsd";

  private final List<Status> status = new ArrayList<Status>();
  protected Node rootNode = new Node();
  private Element rootElement;
  private String schemaResource;


  static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    enableXInclude(factory);
    return factory.newDocumentBuilder();
  }


  private static void enableXInclude(final DocumentBuilderFactory factory) {
    try {
      // Alternative: We set if a system property on the command line is set, for example:
      // -DLog4j.XInclude=true
      factory.setXIncludeAware(true);
    } catch (final UnsupportedOperationException e) {
      LOGGER.warn("The DocumentBuilderFactory [{0}] does not support XInclude: {1}", factory, e);
    } catch (@SuppressWarnings("ErrorNotRethrown") final AbstractMethodError err) {
      LOGGER.warn("The DocumentBuilderFactory [{0}] is out of date and does not support XInclude: {1}", factory, err);
    } catch (final NoSuchMethodError err) {
      // LOG4J2-919
      LOGGER.warn("The DocumentBuilderFactory [{0}] is out of date and does not support XInclude: {1}", factory, err);
    }
    try {
      // Alternative: We could specify all features and values with system properties like:
      // -DLog4j.DocumentBuilderFactory.Feature="http://apache.org/xml/features/xinclude/fixup-base-uris true"
      factory.setFeature(XINCLUDE_FIXUP_BASE_URIS, true);
    } catch (final ParserConfigurationException e) {
      LOGGER.warn("The DocumentBuilderFactory [{0}] does not support the feature [{1}]: {2}", factory,
          XINCLUDE_FIXUP_BASE_URIS, e);
    } catch (@SuppressWarnings("ErrorNotRethrown") final AbstractMethodError err) {
      LOGGER.warn("The DocumentBuilderFactory [{0}] is out of date and does not support setFeature: {1}", factory, err);
    }
    try {
      factory.setFeature(XINCLUDE_FIXUP_LANGUAGE, true);
    } catch (final ParserConfigurationException e) {
      LOGGER.warn("The DocumentBuilderFactory [{0}] does not support the feature [{1}]: {2}", factory,
          XINCLUDE_FIXUP_LANGUAGE, e);
    } catch (@SuppressWarnings("ErrorNotRethrown") final AbstractMethodError err) {
      LOGGER.warn("The DocumentBuilderFactory [{0}] is out of date and does not support setFeature: {1}", factory, err);
    }
  }

  public XmlConfiguration(final ConfigurationSource configSource) {
    final File configFile = configSource.getFile();
    byte[] buffer = null;

    try {
      final InputStream configStream = configSource.getInputStream();
      try {
        buffer = toByteArray(configStream);
      } finally {
        Closer.closeSilently(configStream);
      }
      final InputSource source = new InputSource(new ByteArrayInputStream(buffer));
      source.setSystemId(configSource.getLocation());
      final Document document = newDocumentBuilder().parse(source);
      rootElement = document.getDocumentElement();
      final Map<String, String> attrs = processAttributes(rootNode, rootElement);
    } catch (final SAXException domEx) {
      LOGGER.error("Error parsing {0}", configSource.getLocation(), domEx);
    } catch (final IOException ioe) {
      LOGGER.error("Error parsing {0}", configSource.getLocation(), ioe);
    } catch (final ParserConfigurationException pex) {
      LOGGER.error("Error parsing {0}", configSource.getLocation(), pex);
    }
    if (getName() == null) {
      setName("");
    }
  }

  @Override
  public void setup() {
    if (rootElement == null) {
      LOGGER.error("No logging configuration");
      return;
    }
    constructHierarchy(rootNode, rootElement);
    if (status.size() > 0) {
      for (final Status s : status) {
        LOGGER.error("Error processing element {0}: {1}", s.name, s.errorType);
      }
      return;
    }
    rootElement = null;
  }

  @Override
  protected void doConfigure() {
    setup();
    for (final Node child : rootNode.getChildren()) {
      createConfiguration(child, null);
      if (child.getObject() != null) {
        RecorderConfig recorderConfig = (RecorderConfig) child.getObject();
        addRecorder(recorderConfig.getName(), recorderConfig);
      }
    }
  }

  public void createConfiguration(final Node node, final Event event) {
    for (final Node child : node.getChildren()) {
      createConfiguration(child, event);
    }
    if (node.getClazz() != null) {
      node.setObject(createObject(node, event));
    }
  }

  private Object createObject(final Node node, final Event event) {
    final Class<?> clazz = node.getClazz();

    if (Map.class.isAssignableFrom(clazz)) {
      try {
        return createPluginMap(node);
      } catch (final Exception e) {
        LOGGER.warn("Unable to create Map for {0} of class {1}", clazz.getName(), clazz, e);
      }
    }

    if (Collection.class.isAssignableFrom(clazz)) {
      try {
        return createPluginCollection(node);
      } catch (final Exception e) {
        LOGGER.warn("Unable to create List for {0} of class {1}", clazz.getName(), clazz, e);
      }
    }

    return new ObjectBuilder(clazz).withConfiguration(this).withConfigurationNode(node).forEvent(event).build();
  }

  private static Map<String, ?> createPluginMap(final Node node) {
    final Map<String, Object> map = new LinkedHashMap<String, Object>();
    for (final Node child : node.getChildren()) {
      final Object object = child.getObject();
      map.put(child.getName(), object);
    }
    return map;
  }

  private static Collection<?> createPluginCollection(final Node node) {
    final List<Node> children = node.getChildren();
    final Collection<Object> list = new ArrayList<Object>(children.size());
    for (final Node child : children) {
      final Object object = child.getObject();
      list.add(object);
    }
    return list;
  }

  private void constructHierarchy(final Node node, final Element element) {
    processAttributes(node, element);
    final StringBuilder buffer = new StringBuilder();
    final NodeList list = element.getChildNodes();
    final List<Node> children = node.getChildren();
    for (int i = 0; i < list.getLength(); i++) {
      final org.w3c.dom.Node w3cNode = list.item(i);
      if (w3cNode instanceof Element) {
        final Element child = (Element) w3cNode;
        final String name = getType(child);
        final Class clazz = getClass(child);
        final Node childNode = new Node(node, name, clazz);
        constructHierarchy(childNode, child);
        if (clazz == null && !childNode.hasChildren() && childNode.getValue() != null) {
          node.getAttributes().put(name, childNode.getValue());
        } else {
          children.add(childNode);
        }
      } else if (w3cNode instanceof Text) {
        final Text data = (Text) w3cNode;
        buffer.append(data.getData());
      }
    }

    final String text = buffer.toString().trim();
    if (text.length() > 0 || (!node.hasChildren() && !node.isRoot())) {
      node.setValue(text);
    }
  }

  private String getType(final Element element) {
    final NamedNodeMap attrs = element.getAttributes();
    for (int i = 0; i < attrs.getLength(); ++i) {
      final org.w3c.dom.Node w3cNode = attrs.item(i);
      if (w3cNode instanceof Attr) {
        final Attr attr = (Attr) w3cNode;
        if (attr.getName().equalsIgnoreCase("type")) {
          final String type = attr.getValue();
          attrs.removeNamedItem(attr.getName());
          return type;
        }
      }
    }
    return element.getTagName();
  }

  private Class getClass(final Element element) {
    if (element.getTagName().equalsIgnoreCase("recorder")) {
      return RecorderConfig.class;
    } else {
      final NamedNodeMap attrs = element.getAttributes();
      for (int i = 0; i < attrs.getLength(); ++i) {
        final org.w3c.dom.Node w3cNode = attrs.item(i);
        if (w3cNode instanceof Attr) {
          final Attr attr = (Attr) w3cNode;
          if (attr.getName().equalsIgnoreCase("class")) {
            final String className = attr.getValue();
            attrs.removeNamedItem(attr.getName());
            try {
              final Class<?> clazz = getClass().getClassLoader().loadClass(className);
              return clazz;
            } catch (final ClassNotFoundException e) {
              LOGGER.debug("Plugin [{0}] could not be loaded due to missing classes.", className, e);
            } catch (final VerifyError e) {
              LOGGER.debug("Plugin [{0}] could not be loaded due to verification error.", className, e);
            }

          }
        }
      }
    }

    return null;
  }

  private Map<String, String> processAttributes(final Node node, final Element element) {
    final NamedNodeMap attrs = element.getAttributes();
    final Map<String, String> attributes = node.getAttributes();

    for (int i = 0; i < attrs.getLength(); ++i) {
      final org.w3c.dom.Node w3cNode = attrs.item(i);
      if (w3cNode instanceof Attr) {
        final Attr attr = (Attr) w3cNode;
        if (attr.getName().equals("xml:base")) {
          continue;
        }
        attributes.put(attr.getName(), attr.getValue());
      }
    }
    return attributes;
  }

  private static final int BUF_SIZE = 16384;

  protected static byte[] toByteArray(final InputStream is) throws IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    final byte[] data = new byte[BUF_SIZE];

    while ((nRead = is.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    return buffer.toByteArray();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }


  private enum ErrorType {
    CLASS_NOT_FOUND
  }


  private static class Status {
    private final Element element;
    private final String name;
    private final ErrorType errorType;

    public Status(final String name, final Element element, final ErrorType errorType) {
      this.name = name;
      this.element = element;
      this.errorType = errorType;
    }
  }

}
