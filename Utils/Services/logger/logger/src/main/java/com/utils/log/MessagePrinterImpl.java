package com.utils.log;

public class MessagePrinterImpl extends MessagePrinter {

	@Override
	public void printMessage(MessageLevel messageLevel, String message) {

		switch (messageLevel) {

			case INFO:
			case PROGRESS:
			case STATUS:
				System.out.println(message);
				break;

			case WARNING:
			case ERROR:
                System.err.println(message);
                break;

			case EXCEPTION:
			    if(Logger.isDebugMode()) {
                    System.err.println(message);
                }
				break;
		}
	}
}
