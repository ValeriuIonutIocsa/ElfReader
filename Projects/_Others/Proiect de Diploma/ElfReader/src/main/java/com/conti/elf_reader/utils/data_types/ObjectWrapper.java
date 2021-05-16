package com.conti.elf_reader.utils.data_types;

public class ObjectWrapper<T> {

	private T value = null;

	public ObjectWrapper() {

	}

	public ObjectWrapper(T value) {

		this.value = value;
	}

	public void setValue(T value) {

		this.value = value;
	}

	public T getValue() {

		return value;
	}

	public boolean isNull() {

		return value == null;
	}
}
