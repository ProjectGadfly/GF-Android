package com.forvm.gadfly.projectgadfly;

import java.io.Serializable;

/**
 * Created by papak on 3/21/2017.
 */

public class Representatives implements Serializable {

    public String name, phone_number, email, district, state, photo_url, party, position;

    /**
     * Constructor for representatives class
     * @param name Name of the representative
     * @param phone_number Phone number of the representative
     * @param photo_url Url to the photo of the representative
     * @
     */
    Representatives(String name, String phone_number, String photo_url, String party, String position) {
        this.name = name;
        this.phone_number = phone_number;
        this.email = "";
        this.district = "";
        this.state = "";
        this.photo_url = photo_url;
        this.party = party;
        this.position = position;
    }

    // Constructor for future development with more fields for legislator
    Representatives(String name, String phone_number, String email, String district,
                    String state, String photo_url, String party, String position) {
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
        this.district = district;
        this.state = state;
        this.photo_url = photo_url;
        this.party = party;
        this.position = position;
    }
}
