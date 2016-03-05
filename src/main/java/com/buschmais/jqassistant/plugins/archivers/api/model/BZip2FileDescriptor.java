package com.buschmais.jqassistant.plugins.archivers.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.ArchiveDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.ValidDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("BZip2")
public interface BZip2FileDescriptor extends ArchiveDescriptor,
                                             ValidDescriptor {
}
