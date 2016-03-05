package com.buschmais.jqassistant.plugins.archivers.api.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.jqassistant.plugins.archivers.api.model.BZip2FileDescriptor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class BZip2FileScannerPluginIT extends AbstractPluginIT {

    @After
    public void commitTx() {
        store.commitTransaction();
    }

    @Test
    public void scannerHandlesSmallPackedTextFileCorrectly() throws Exception {
        store.beginTransaction();

        URL url4Archive = getClass().getResource("/bzip2/valid/small.txt.bz2");
        String pathOfArchive = url4Archive.getFile();
        File fileOfArchive  = new File(pathOfArchive);

        BZip2FileDescriptor descriptor = getScanner().scan(fileOfArchive, pathOfArchive, DefaultScope.NONE);

        assertThat(descriptor, CoreMatchers.notNullValue());
        assertThat(descriptor.getFileName(), equalTo(pathOfArchive));
        assertThat(descriptor.getContains(), hasSize(1));
        assertThat(descriptor.getContains(), notNullValue());
        assertThat(descriptor.isValid(), is(true));
    }

    @Test
    public void bzip2DescriptorCanBeFoundViaItsLabels() {
        store.beginTransaction();

        URL url4Archive = getClass().getResource("/bzip2/valid/small.txt.bz2");
        String pathOfArchive = url4Archive.getFile();
        File fileOfArchive = new File(pathOfArchive);

        getScanner().scan(fileOfArchive, pathOfArchive, DefaultScope.NONE);

        List<BZip2FileDescriptor> result = query("MATCH (b:BZip2:Archive:Container) RETURN b").getColumn("b");

        assertThat(result, notNullValue());
        assertThat(result, hasSize(1));
        assertThat(result.stream().findFirst().get().getFileName(), endsWith("small.txt.bz2"));
    }

    @Test
    public void compressedFileIsContainedInTheArchive() throws Exception {
        store.beginTransaction();

        URL url4Archive = getClass().getResource("/bzip2/valid/small.txt.bz2");
        String pathOfArchive = url4Archive.getFile();
        File fileOfArchive = new File(pathOfArchive);

        getScanner().scan(fileOfArchive, pathOfArchive, DefaultScope.NONE);

        List<BZip2FileDescriptor> result = query("MATCH (b:BZip2:Archive:Container) RETURN b").getColumn("b");

        BZip2FileDescriptor bzDesc = result.stream().findFirst().get();

        assertThat(bzDesc.getContains(), hasSize(1));

        FileDescriptor fileDescriptor = bzDesc.getContains().get(0);

        assertThat(fileDescriptor.getFileName(), endsWith(".txt"));
    }


}
