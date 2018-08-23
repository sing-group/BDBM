/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package es.uvigo.ei.sing.bdbm.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;

public class LogConfiguration {
	public static final String EXECUTION_STD_MARKER_LABEL = "EXECUTION_STD";
	public static final String EXECUTION_ERROR_MARKER_LABEL = "EXECUTION_ERROR";
	
	public static final Marker MARKER_EXECUTION_STD;
	public static final Marker MARKER_EXECUTION_ERROR;
	
	static {
		final BasicMarkerFactory markerFactory = new BasicMarkerFactory();
		MARKER_EXECUTION_STD = markerFactory.getMarker(LogConfiguration.EXECUTION_STD_MARKER_LABEL);
		MARKER_EXECUTION_ERROR = markerFactory.getMarker(LogConfiguration.EXECUTION_ERROR_MARKER_LABEL);
	}

	public static boolean hasAnyExecutionAppender() {
		final Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		
		if (root instanceof AppenderAttachable) {
			final AppenderAttachable<?> rootAA = (AppenderAttachable<?>) root;
			
			return rootAA.getAppender(EXECUTION_STD_MARKER_LABEL) instanceof ExecutionObservableAppender ||
				rootAA.getAppender(EXECUTION_ERROR_MARKER_LABEL) instanceof ExecutionObservableAppender;
		} else {
			return false;
		}
	}
	
	public static boolean hasStandardExecutionAppender() {
		return hasExecutionAppender(EXECUTION_STD_MARKER_LABEL);
	}
	
	public static boolean hasErrorExecutionAppender() {
		return hasExecutionAppender(EXECUTION_ERROR_MARKER_LABEL);
	}
	
	public static ExecutionObservableAppender getStandardExecutionAppender() {
		return getExecutionAppender(EXECUTION_STD_MARKER_LABEL);
	}
	
	public static ExecutionObservableAppender getErrorExecutionAppender() {
		return getExecutionAppender(EXECUTION_ERROR_MARKER_LABEL);
	}
	
	private static boolean hasExecutionAppender(String appenderName) {
		return getExecutionAppender(appenderName) != null;
	}

	private static ExecutionObservableAppender getExecutionAppender(
		final String appenderName
	) {
		final Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		
		if (root instanceof AppenderAttachable) {
			final AppenderAttachable<?> rootAA = (AppenderAttachable<?>) root;
			
			final Appender<?> appender = rootAA.getAppender(appenderName);
			
			if (appender instanceof ExecutionObservableAppender) {
				return (ExecutionObservableAppender) appender;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
