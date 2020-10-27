package io.jenetics.incubator.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LifecycleTest {

	final static class Invokable {
		final AtomicBoolean called = new AtomicBoolean(false);
		final Supplier<Exception> error;

		Invokable(final Supplier<Exception> error) {
			this.error = error;
		}

		void invoke() throws Exception {
			called.set(true);
			final var e = error != null ? error.get() : null;
			if (e != null) {
				throw e;
			}
		}
	}

	@Test(dataProvider = "invokables")
	public void invokeAll0(
		final List<Invokable> objects,
		final Class<?> errorType,
		final Class<?> suppressedType
	) {
		Assert.assertTrue(objects.stream().noneMatch(i -> i.called.get()));

		final var exception = Lifecycle.invokeAll0(Invokable::invoke, objects);

		Assert.assertTrue(objects.stream().allMatch(i -> i.called.get()));
		if (exception != null) {
			Assert.assertEquals(exception.getClass(), errorType);

			if (suppressedType != null) {
				Assert.assertEquals(exception.getSuppressed().length, 1);
				Assert.assertEquals(
					exception.getSuppressed()[0].getClass(),
					suppressedType
				);
			}
		}
	}

	@DataProvider
	public Object[][] invokables() {
		return new Object[][] {
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(null)
				),
				null,
				null
			},
			{
				List.of(new Invokable(IllegalArgumentException::new)),
				IllegalArgumentException.class,
				null
			},
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(IllegalArgumentException::new)
				),
				IllegalArgumentException.class,
				null
			},
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(IllegalArgumentException::new),
					new Invokable(null),
					new Invokable(null)
				),
				IllegalArgumentException.class,
				null
			},
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(UnsupportedOperationException::new),
					new Invokable(null),
					new Invokable(NoSuchFieldException::new),
					new Invokable(null),
					new Invokable(null)
				),
				UnsupportedOperationException.class,
				NoSuchFieldException.class
			}
		};
	}

	@Test(expectedExceptions = IOException.class)
	public void close1() throws IOException {
		final var count = new AtomicInteger();

		try {
			Lifecycle.close(List.of(
				count::incrementAndGet,
				count::incrementAndGet,
				() -> { throw new IOException(); },
				count::incrementAndGet
			));
		} catch (IOException e) {
			Assert.assertEquals(3, count.get());
			throw e;
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void close2() throws IOException {
		final var count = new AtomicInteger();

		try {
			Lifecycle.close(List.of(
				count::incrementAndGet,
				count::incrementAndGet,
				() -> { throw new IllegalArgumentException(); },
				count::incrementAndGet,
				() -> { throw new IOException(); }
			));
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(3, count.get());
			Assert.assertEquals(e.getSuppressed().length, 1);
			Assert.assertEquals(
				e.getSuppressed()[0].getClass(),
				IOException.class
			);
			throw e;
		}
	}

}