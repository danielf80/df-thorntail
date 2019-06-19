package com.df.thorntail;

import javax.enterprise.inject.Model;

/**
 * @author Daniel Filgueiras
 * @since 2019-06-14
 *
 */

@Model
public class Message {
	public String say() {
        return "Hello from JSF";
    }
}
