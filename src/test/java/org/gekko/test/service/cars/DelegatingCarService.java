package org.gekko.test.service.cars;

public class DelegatingCarService {

	private CarService delegate;

	public void setDelegate(CarService delegate) {
		this.delegate = delegate;
	}

	public CarService getDelegate() {
		return delegate;
	}

}
