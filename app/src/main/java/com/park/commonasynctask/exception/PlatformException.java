package com.park.commonasynctask.exception;

/**
 * Filename		: PlatformException.java
 * Function		:
 * Comment		: Exceptionのベースクラス
 * History		: 2016/03/30, su min park, develop
 *
 * @version 1.0
 * @author  su min park
 * @since   JDK 1.7
 */
public class PlatformException extends Exception {
    private static final long serialVersionUID = 8493367631615129978L;

    public PlatformException(String message) {
        super(message);
    }

    public PlatformException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
