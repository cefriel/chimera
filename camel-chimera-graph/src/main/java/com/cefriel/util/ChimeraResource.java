package com.cefriel.util;

public sealed interface ChimeraResource permits ChimeraResource.ClassPathResource, ChimeraResource.FileResource, ChimeraResource.HeaderResource, ChimeraResource.HttpResource, ChimeraResource.PropertyResource
{
    record FileResource(String url, String serializationFormat) implements ChimeraResource {}
    record HttpResource(String url, String serializationFormat, TypeAuthConfig authConfig) implements ChimeraResource {}
    record HeaderResource(String url, String serializationFormat) implements ChimeraResource {}
    record PropertyResource(String url, String serializationFormat) implements ChimeraResource {}
    record ClassPathResource(String url, String serializationFormat) implements ChimeraResource {}
}