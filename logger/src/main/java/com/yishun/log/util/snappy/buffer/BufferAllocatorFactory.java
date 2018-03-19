package com.yishun.log.util.snappy.buffer;

/**
 *
 */
public interface BufferAllocatorFactory
{

    BufferAllocator getBufferAllocator(int minSize);
}

