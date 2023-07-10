package org.apache.shade.net.jpountz.xxhash;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


final class StreamingXXHash32JNI extends org.apache.shade.net.jpountz.xxhash.StreamingXXHash32 {

  static class Factory implements org.apache.shade.net.jpountz.xxhash.StreamingXXHash32.Factory {

    public static final org.apache.shade.net.jpountz.xxhash.StreamingXXHash32.Factory INSTANCE = new Factory();

    @Override
    public StreamingXXHash32 newStreamingHash(int seed) {
      return new StreamingXXHash32JNI(seed);
    }

  }

  private long state;

  StreamingXXHash32JNI(int seed) {
    super(seed);
    state = org.apache.shade.net.jpountz.xxhash.XXHashJNI.XXH32_init(seed);
  }

  private void checkState() {
    if (state == 0) {
      throw new AssertionError("Already finalized");
    }
  }

  @Override
  public void reset() {
    checkState();
    org.apache.shade.net.jpountz.xxhash.XXHashJNI.XXH32_free(state);
    state = org.apache.shade.net.jpountz.xxhash.XXHashJNI.XXH32_init(seed);
  }

  @Override
  public int getValue() {
    checkState();
    return org.apache.shade.net.jpountz.xxhash.XXHashJNI.XXH32_intermediateDigest(state);
  }

  @Override
  public void update(byte[] bytes, int off, int len) {
    checkState();
    org.apache.shade.net.jpountz.xxhash.XXHashJNI.XXH32_update(state, bytes, off, len);
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    // free memory
    org.apache.shade.net.jpountz.xxhash.XXHashJNI.XXH32_free(state);
    state = 0;
  }

}
