package com.olrep.theweatherapp.datasources;

/**
 * common enum to define what call would be made on db
 * this is used so that a common method can be implemented on bg thread for
 * db ops
 */
public enum Operation {
    INSERT, UPDATE, DELETE
}
