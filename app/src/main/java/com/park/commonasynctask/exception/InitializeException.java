package com.park.commonasynctask.exception;

/**
 * Filename		: InitializeException.java
 * Function		:
 * Comment		: 初期化に関するException
 * History		: 2016/03/30, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public class InitializeException extends PlatformException {

    public InitializeException(String message) {
        super(message);
    }

    public InitializeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
