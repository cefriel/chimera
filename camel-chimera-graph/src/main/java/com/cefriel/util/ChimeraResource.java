package com.cefriel.util;

public sealed interface ChimeraResource permits ChimeraResource.ClassPathResource, ChimeraResource.FileResource, ChimeraResource.HeaderResource, ChimeraResource.HttpResource, ChimeraResource.PropertyResource, ChimeraResource.VariableResource
{
    record FileResource(String url, String serializationFormat) implements ChimeraResource {
        public java.nio.file.Path getPath() {
            String prefix = ChimeraResourceConstants.FILE_PREFIX;
            String pathStr = url.startsWith(prefix) ? url.substring(prefix.length()) : url;
            return java.nio.file.Paths.get(pathStr);
        }
    }
    record HttpResource(String url, String serializationFormat, TypeAuthConfig authConfig) implements ChimeraResource {}
    record HeaderResource(String url, String serializationFormat) implements ChimeraResource {}
    record PropertyResource(String url, String serializationFormat) implements ChimeraResource {}
    record VariableResource(String url, String serializationFormat) implements ChimeraResource {}
    record ClassPathResource(String url, String serializationFormat) implements ChimeraResource {
        public java.nio.file.Path getPath() {
            String prefix = ChimeraResourceConstants.CLASSPATH_PREFIX;
            String pathStr = url.startsWith(prefix) ? url.substring(prefix.length()) : url;
            return java.nio.file.Paths.get(pathStr);
        }
    }
}