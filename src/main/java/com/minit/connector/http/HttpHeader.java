package com.minit.connector.http;

public class HttpHeader {
    public static final int INITIAL_NAME_SIZE = 64;
    public static final int INITIAL_VALUE_SIZE = 512;
    public static final int MAX_NAME_SIZE = 128;
    public static final int MAX_VALUE_SIZE = 1024;

    public char[] name;
    public int nameEnd;
    public char[] value;
    public int valueEnd;
    protected int hashCode = 0;

    public HttpHeader() {
        this(new char[INITIAL_NAME_SIZE], 0, new char[INITIAL_VALUE_SIZE], 0);
    }
    public HttpHeader(char[] name, int nameEnd, char[] value, int valueEnd) {
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    public HttpHeader(String name, String value) {
        this.name = name.toLowerCase().toCharArray();
        this.nameEnd = name.length();
        this.value = value.toCharArray();
        this.valueEnd = value.length();
    }

    public void recycle() {
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

}
