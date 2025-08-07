

package cache.openrs.util.tukaani;

import java.io.InputStream;

interface FilterDecoder extends FilterCoder {
    int getMemoryUsage();
    InputStream getInputStream(InputStream in);
}
