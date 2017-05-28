package com.pwr.zpi.semantic;

import com.pwr.zpi.semantic.Identifier;

/**
 * Example of identifier.
 */
public class QRCode extends Identifier {

    String id;

    public QRCode(String id) {
        this.id = id;
    }

    public String getIdNumber() {
        return id;
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "id='" + id + '\'' +
                '}';
    }
}