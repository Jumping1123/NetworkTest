package com.example.jumping.networktest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    public static final int SHOW_RESPONSE = 0;
    private Button sendrequest;
    private TextView responsetext;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    responsetext.setText(response);

            }
        }
    };

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendrequest = (Button) findViewById(R.id.send_request);
        responsetext = (TextView) findViewById(R.id.response_text);
        sendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendrequestwithhttpurlconnection();
                String address = "http://www.baidu.com";
                HttpUtil.sendhttprequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void sendrequestwithhttpurlconnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://10.0.2.2/get_data.xml");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    parseXMLWithPull(response.toString());
                    parseXMLWithSAX(response.toString());

                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    message.obj = response.toString();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseXMLWithPull(String xmldata) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventtype = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while (eventtype != XmlPullParser.END_DOCUMENT) {
                String nodename = xmlPullParser.getName();
                switch (eventtype) {
                    case XmlPullParser.START_TAG:
                        if ("id".equals(nodename)) {
                            id = xmlPullParser.nextText();
                        } else if ("name".equals(nodename)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodename)) {
                            version = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("app".equals(nodename)) {
                            Log.d("MainActivity_pull", "id is " + id);
                            Log.d("MainActivity_Pull", "name is " + name);
                            Log.d("MainActivity_Pull", "version is " + version);
                        }
                        break;
                    default:
                        break;
                }
                eventtype = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLWithSAX(String xmldata) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler contentHandler = new ContentHandler();
            xmlReader.setContentHandler(contentHandler);
            xmlReader.parse(new InputSource(new StringReader(xmldata)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
