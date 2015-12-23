/*
 * #%L
 * BDBM API
 * %%
 * Copyright (C) 2014 - 2015 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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
package es.uvigo.ei.sing.bdbm.persistence;

public class EntityValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	private final Object entity;

	public EntityValidationException(Object entity) {
		this.entity = entity;
	}

	public EntityValidationException(String message, Object entity) {
		super(message);
		this.entity = entity;
	}

	public EntityValidationException(Throwable cause, Object entity) {
		super(cause);
		this.entity = entity;
	}

	public EntityValidationException(String message, Throwable cause, Object entity) {
		super(message, cause);
		this.entity = entity;
	}

	public Object getEntity() {
		return entity;
	}
}
