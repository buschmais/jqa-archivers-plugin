package com.buschmais.jqassistant.plugins.archivers.api.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.AbstractFileResource;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.buschmais.jqassistant.plugin.common.impl.scanner.BufferedFileResource;
import com.buschmais.jqassistant.plugins.archivers.api.model.BZip2FileDescriptor;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Scanner plugin for archives compressed with the BZip2 algorithm.
 */
@ScannerPlugin.Requires(FileDescriptor.class)
public class BZip2FileScannerPlugin
    extends AbstractScannerPlugin<FileResource, BZip2FileDescriptor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BZip2FileScannerPlugin.class);

    @Override
    public boolean accepts(FileResource resource, String path, Scope scope)
        throws IOException {
        boolean accepted = BZip2Utils.isCompressedFilename(path);

        return accepted;
    }

    @Override
    public BZip2FileDescriptor scan(final FileResource item, String path,
                                    Scope scope, Scanner scanner)
        throws IOException {
        ScannerContext context = scanner.getContext();
        Store store = context.getStore();

        FileDescriptor fd = context.peek(FileDescriptor.class);
        BZip2FileDescriptor bz2FileDescriptor = store.addDescriptorType(fd, BZip2FileDescriptor.class);
        String uncompressedPath = getUncompressedFilename(path);

        AbstractFileResource scannedResource = getCompressedResource(item);

        try (FileResource fileResource = new BufferedFileResource(scannedResource)) {
            FileDescriptor entryDescriptor = scanner.scan(fileResource, uncompressedPath, scope);
            bz2FileDescriptor.getContains().add(entryDescriptor);
            bz2FileDescriptor.setValid(true);
        } catch (IOException ioe) {
            LOGGER.warn("Cannot read BZip2 file '" + path + "'.", ioe);
            bz2FileDescriptor.setValid(false);
        }

        return bz2FileDescriptor;
    }

    protected AbstractFileResource getCompressedResource(final FileResource item) {
        return new AbstractFileResource() {
                @Override
                public InputStream createStream() throws IOException {
                    return new BZip2CompressorInputStream(item.createStream());
                }
            };
    }

    protected String getUncompressedFilename(String path) {
        String uncompressedFileName = BZip2Utils.getUncompressedFilename(path);

        return uncompressedFileName;
    }
}
