package com.buschmais.jqassistant.plugins.archivers.api.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.AbstractFileResource;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.buschmais.jqassistant.plugins.archivers.api.model.BZip2FileDescriptor;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class BZip2FileScannerPluginTest {

    private BZip2FileScannerPlugin plugin = new BZip2FileScannerPlugin();


    @Test
    public void acceptMethodCanHandleMethodsWithDotAsLastCar() throws Exception {
        FileResource resource = Mockito.mock(FileResource.class);
        String path = "/a/b/c/archive.a42.bz.";

        boolean accepted = plugin.accepts(resource, path, DefaultScope.NONE);

        assertThat(accepted, is(false));
    }

    @Test
    public void scannerAcceptsFilesWithEndingBZ() throws Exception {
        FileResource resource = Mockito.mock(FileResource.class);
        String path = "/a/b/c/archive.a42.bz";

        boolean accepted = plugin.accepts(resource, path, DefaultScope.NONE);

        assertThat(accepted, is(true));
    }

    @Test
    public void scannerAcceptsFilesWithEndingBZ2() throws Exception {
        FileResource resource = Mockito.mock(FileResource.class);
        String path = "/a/b/c/archive.a42.bz2";

        boolean accepted = plugin.accepts(resource, path, DefaultScope.NONE);

        assertThat(accepted, is(true));
    }

    @Test
    public void scannerAcceptsFilesWithEndingTBZ() throws Exception {
        FileResource resource = Mockito.mock(FileResource.class);
        String path = "/a/b/c/archive.a42.tbz";

        boolean accepted = plugin.accepts(resource, path, DefaultScope.NONE);

        assertThat(accepted, is(true));
    }

    @Test
    public void scannerAcceptsFilesWithEndingTBZ2() throws Exception {
        FileResource resource = Mockito.mock(FileResource.class);
        String path = "/a/b/c/archive.a42.tbz2";

        boolean accepted = plugin.accepts(resource, path, DefaultScope.NONE);

        assertThat(accepted, is(true));
    }

    @Test
    public void containedFileWillGetCorrectEndingForArchiveExtensionBZ() throws Exception {
        String path = "/a/b/c/file.ext.bz2";

        String filename = plugin.getUncompressedFilename(path);

        assertThat(filename, endsWith(".ext"));
    }

    @Test
    public void containedFileWillGetCorrectEndingForArchiveExtensionBZ2() throws Exception {
        String path = "/a/b/c/file.ext.bz2";

        String filename = plugin.getUncompressedFilename(path);

        assertThat(filename, endsWith(".ext"));
    }

    @Test
    public void containedFileWillGetCorrectEndingForArchiveExtensionTBZ() throws Exception {
        String path = "/a/b/c/file.tbz";

        String filename = plugin.getUncompressedFilename(path);

        assertThat(filename, endsWith(".tar"));
    }

    @Test
    public void containedFileWillGetCorrectEndingForArchiveExtensionTBZ2() throws Exception {
        String path = "/a/b/c/file.tbz2";

        String filename = plugin.getUncompressedFilename(path);

        assertThat(filename, endsWith(".tar"));
    }

    @Test
    public void failureWhileScanningArchiveContentSetsValidToFalse() throws IOException {
        FileDescriptor fdForArchive = Mockito.mock(FileDescriptor.class);
        BZip2FileDescriptor bzipDescriptor = Mockito.mock(BZip2FileDescriptor.class);


        Store store = Mockito.mock(Store.class);
        doReturn(bzipDescriptor).when(store).addDescriptorType(eq(fdForArchive), Mockito.any(Class.class));

        ScannerContext context = Mockito.mock(ScannerContext.class);
        doReturn(store).when(context).getStore();
        doReturn(fdForArchive).when(context).peek(any(Class.class));

        Scanner scanner = Mockito.mock(Scanner.class);
        doReturn(context).when(scanner).getContext();
        doThrow(IOException.class).when(scanner).scan(anyObject(), anyString(), any(Scope.class));

        FileResource resourceForArchive = Mockito.mock(FileResource.class);

        BZip2FileDescriptor descriptor = plugin.scan(resourceForArchive, "/a/b/file.ext.bz2",
                                                     DefaultScope.NONE, scanner);

        verify(descriptor).setValid(eq(false));
    }

}
