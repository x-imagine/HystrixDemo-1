package com.solstice.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class BookService {

    private final RestTemplate restTemplate;

    public BookService(RestTemplate rest) {
        this.restTemplate = rest;
    }

    /*
    The command below renames the Hystrix command to 'BookListCall'
    from it's method name of 'readingList'.  It has also been configured to NOT consider
    HTTP Forbidden errors as failures. Preventing the circuit from opening if too many AUTH Failures happen.
    NOTE: This dependency does not have security on it, this is for show.
     */
    @HystrixCommand(
      commandKey = "booklistcall",
      fallbackMethod = "reliable",
      ignoreExceptions = {HttpClientErrorException.Forbidden.class}
    )
    public String readingList() {
        URI uri = URI.create("http://book-list-lnl-demo.cfapps.io/recommended");

        return this.restTemplate.getForObject(uri, String.class);
    }


    /*
    This is our fallback method that is used to serve a static response in the event that the above
    method fails to return.
     */
    public String reliable() {
        return "Our Recommendation Service is unavailable but you should read : Cloud Native Java (O'Reilly)";
    }

}
