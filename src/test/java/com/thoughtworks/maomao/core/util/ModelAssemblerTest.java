package com.thoughtworks.maomao.core.util;

import com.thoughtworks.maomao.example.model.Book;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class ModelAssemblerTest {

    private ModelAssembler modelAssembler;
    private Map<String,String[]> map;
    private PrimitiveTypeModel primitiveTypeModel;

    @Before
    public void setUp() {
        modelAssembler = new ModelAssembler();
        map = new HashMap<String, String[]>();
    }

    @Test
    public void should_assemble_with_type_string() {
        map.put("primitiveTypeModel.stringProperty", getArray("Hello."));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals("Hello.", primitiveTypeModel.getStringProperty());
    }

    @Test
    public void should_assemble_with_type_integer() {
        map.put("primitiveTypeModel.integerProperty", getArray("7"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Integer(7), primitiveTypeModel.getIntegerProperty());
    }

    @Test
    public void should_assemble_with_type_short() {
        map.put("primitiveTypeModel.shortProperty", getArray("7"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Short("7"), primitiveTypeModel.getShortProperty());
    }

    @Test
    public void should_assemble_with_type_long() {
        map.put("primitiveTypeModel.longProperty", getArray("7"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Long(7l), primitiveTypeModel.getLongProperty());
    }

    @Test
    public void should_assemble_with_type_float() {
        map.put("primitiveTypeModel.floatProperty", getArray("7.8"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Float(7.8f), primitiveTypeModel.getFloatProperty());
    }

    @Test
    public void should_assemble_with_type_double() {
        map.put("primitiveTypeModel.doubleProperty", getArray("7.8"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Double(7.8d), primitiveTypeModel.getDoubleProperty());
    }

    @Test
    public void should_assemble_with_type_character() {
        map.put("primitiveTypeModel.characterProperty", getArray("o"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Character('o'), primitiveTypeModel.getCharacterProperty());
    }

    @Test
    public void should_assemble_with_type_boolean() {
        map.put("primitiveTypeModel.booleanProperty", getArray("false"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Boolean(false), primitiveTypeModel.getBooleanProperty());
    }

    @Test
    public void should_assemble_with_type_byte() {
        map.put("primitiveTypeModel.byteProperty", getArray("87"));
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(new Byte("87"), primitiveTypeModel.getByteProperty());
    }

    @Test
    public void should_assemble_with_string_array() {
        String[] strings = new String[]{"x", "y", "z"};
        map.put("primitiveTypeModel.stringArray", strings);
        primitiveTypeModel = modelAssembler.assembleModel(map, PrimitiveTypeModel.class);
        assertEquals(strings, primitiveTypeModel.getStringArray());
    }

    @Test
    public void should_assemble_with_complex_type() {
        String bookName = "Go, Mavericks!";
        String commentContent = "We need win more games.";
        String commentAuthor = "Kidd";

        map.put("book.id", getArray("87"));
        map.put("book.name", getArray(bookName));
        map.put("book.comment.content", getArray(commentContent));
        map.put("book.comment.author", getArray(commentAuthor));

        Book book = modelAssembler.assembleModel(map, Book.class);

        assertEquals(new Integer(87), book.getId());
        assertEquals(bookName, book.getName());
        assertEquals(commentContent, book.getComment().getContent());
        assertEquals(commentAuthor, book.getComment().getAuthor());
    }

    private String[] getArray(String string) {
        return new String[] {string};
    }

}
