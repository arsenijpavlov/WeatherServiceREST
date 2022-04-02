package com.Riddick;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws MalformedURLException {
        Service weatherService = Service.create(
                new URL("http://localhost/weather?wsdl"),
                new QName("http://weather", "ServerService")
        );
        Server helloPort = weatherService.getPort(Server.class);
        System.out.println(helloPort.getWeatherFromTable());
    }

    @WebService(targetNamespace = "http://weather")
    public interface Server{
        String getWeatherFromTable();
    }
}
