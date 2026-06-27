package com.saucedemo.testdata;

/** Expected user-facing messages, kept in one place so assertions stay readable. */
public final class Messages {

    // Login errors
    public static final String LOCKED_OUT_USER =
            "Epic sadface: Sorry, this user has been locked out.";
    public static final String INVALID_CREDENTIALS =
            "Epic sadface: Username and password do not match any user in this service";
    public static final String USERNAME_REQUIRED =
            "Epic sadface: Username is required";
    public static final String PASSWORD_REQUIRED =
            "Epic sadface: Password is required";

    // Checkout validation errors
    public static final String FIRST_NAME_REQUIRED = "Error: First Name is required";
    public static final String LAST_NAME_REQUIRED  = "Error: Last Name is required";
    public static final String POSTAL_CODE_REQUIRED = "Error: Postal Code is required";

    // Order confirmation
    public static final String ORDER_COMPLETE_HEADER = "Thank you for your order!";

    private Messages() {
    }
}
