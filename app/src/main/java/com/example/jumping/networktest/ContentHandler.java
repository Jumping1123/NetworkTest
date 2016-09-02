package com.example.jumping.networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Jumping on 2016/8/19.
 */
public class ContentHandler extends DefaultHandler {
    private String nodename;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodename = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("id".equals(nodename)) {
            id.append(ch, start, length);
        } else if ("name".equals(nodename)) {
            name.append(ch, start, length);

        } else if ("version".equals(nodename)) {
            version.append(ch, start, length);
        }
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)) {
            Log.d("ContentHandler_SAX", "id is " + id.toString().trim());
            Log.d("ContentHandler_SAX", "name is " + name.toString().trim());
            Log.d("ContentHandler_SAX", "version is " + version.toString().trim());
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }
}
