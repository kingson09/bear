package com.test.snappy.buffer;

/**
 *
 */
public interface BufferAllocatorFactory
{

    BufferAllocator getBufferAllocator(int minSize);
}

