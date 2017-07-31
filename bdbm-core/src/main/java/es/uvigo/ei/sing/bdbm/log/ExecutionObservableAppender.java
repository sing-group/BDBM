/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

import java.util.List;
import java.util.Observable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;

public class ExecutionObservableAppender extends Observable implements Appender<ILoggingEvent> {
	private final AppenderBase<ILoggingEvent> delegatedAppender;
	
	public ExecutionObservableAppender() {
		this.delegatedAppender = new AppenderBase<ILoggingEvent>() {
			protected void append(ILoggingEvent eventObject) {
				ExecutionObservableAppender.this.notifyChange(eventObject.getFormattedMessage());
			}
		};
	}
	
	protected synchronized void notifyChange(String message) {
		this.setChanged();
		this.notifyObservers(message);
	}
	
	@Override
	public void start() {
		this.delegatedAppender.start();
	}

	@Override
	public void stop() {
		this.delegatedAppender.stop();
	}

	@Override
	public boolean isStarted() {
		return this.delegatedAppender.isStarted();
	}

	@Override
	public void setContext(Context context) {
		this.delegatedAppender.setContext(context);
	}

	@Override
	public Context getContext() {
		return this.delegatedAppender.getContext();
	}

	@Override
	public void addStatus(Status status) {
		this.delegatedAppender.addStatus(status);
	}

	@Override
	public void addInfo(String msg) {
		this.delegatedAppender.addInfo(msg);
	}

	@Override
	public void addInfo(String msg, Throwable ex) {
		this.delegatedAppender.addInfo(msg, ex);
	}

	@Override
	public void addWarn(String msg) {
		this.delegatedAppender.addWarn(msg);
	}

	@Override
	public void addWarn(String msg, Throwable ex) {
		this.delegatedAppender.addWarn(msg, ex);
	}

	@Override
	public void addError(String msg) {
		this.delegatedAppender.addError(msg);
	}

	@Override
	public void addError(String msg, Throwable ex) {
		this.delegatedAppender.addError(msg, ex);
	}

	@Override
	public void addFilter(Filter<ILoggingEvent> newFilter) {
		this.delegatedAppender.addFilter(newFilter);
	}

	@Override
	public void clearAllFilters() {
		this.delegatedAppender.clearAllFilters();
	}

	@Override
	public List<Filter<ILoggingEvent>> getCopyOfAttachedFiltersList() {
		return this.delegatedAppender.getCopyOfAttachedFiltersList();
	}

	@Override
	public FilterReply getFilterChainDecision(ILoggingEvent event) {
		return this.delegatedAppender.getFilterChainDecision(event);
	}

	@Override
	public String getName() {
		return this.delegatedAppender.getName();
	}

	@Override
	public void doAppend(ILoggingEvent event) throws LogbackException {
		this.delegatedAppender.doAppend(event);
	}

	@Override
	public void setName(String name) {
		this.delegatedAppender.setName(name);
	}
}
