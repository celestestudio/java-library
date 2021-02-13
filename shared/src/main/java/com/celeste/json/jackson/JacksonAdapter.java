package com.celeste.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class JacksonAdapter {

    public final ObjectMapper factory = new ObjectMapper();

    public <T> T fromJson(final String json, final Class<T> clazz) {
        try {
            return factory.readValue(json, clazz);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }

        return null;
    }
    public <T> String toJson(final T value) {
        try {
            return factory.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public ObjectNode createNode() {
        return factory.createObjectNode();
    }

    public ObjectNode getNode(final String context)  {
        try {
            return factory.readValue(context, ObjectNode.class);
        } catch (JsonProcessingException exception) {
            System.out.println("Error while returning node, " + exception.getMessage());
        }

        return null;
    }

    public ObjectNode addArrayToNode(final ObjectNode node, final ArrayList<?> list, final String value)  {
        final ArrayNode arrayNode = factory.valueToTree(list);
        node.putArray(value).addAll(arrayNode);

        return node;
    }

    public ArrayList<?> getArrayFromNode(final ObjectNode node, final String value)  {
        return factory.convertValue(node.get(value), ArrayList.class);
    }

    public JsonNode get(final ObjectNode node, final String value) {
        return node.get(value);
    }

    public String getString(final ObjectNode node, final String value) {
        return get(node, value).textValue();
    }

    public int getInt(final ObjectNode node, final String value) {
        return get(node, value).intValue();
    }

    public boolean getBoolean(final ObjectNode node, final String value) {
        return get(node, value).booleanValue();
    }

    public Double getDouble(final ObjectNode node, final String value) {
        return get(node, value).doubleValue();
    }

    public float getFloat(final ObjectNode node, final String value) {
        return get(node, value).floatValue();
    }


}
