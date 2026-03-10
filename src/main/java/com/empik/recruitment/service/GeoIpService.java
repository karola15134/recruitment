package com.empik.recruitment.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CountryResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;

@Service
public class GeoIpService {

    private final DatabaseReader dbReader;

    public GeoIpService() throws Exception {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("geoip/GeoLite2-Country.mmdb");

        dbReader = new DatabaseReader.Builder(is).build();
    }

    public String getCountry(String ip) {
        try {
            if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
                return "LOCAL";
            }
            InetAddress ipAddress = InetAddress.getByName(ip);
            return dbReader.country(ipAddress).getCountry().getIsoCode();
        } catch (AddressNotFoundException e) {
            return "UNKNOWN";
        } catch (Exception e) {
            return "ERROR";
        }
    }
}
