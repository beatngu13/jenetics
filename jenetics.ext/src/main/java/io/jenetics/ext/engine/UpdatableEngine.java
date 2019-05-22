/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.engine;

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class UpdatableEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		EvolutionStreamable<G, C>,
		Updatable<EvolutionStreamable<G, C>>
{

	private final CopyOnWriteArrayList<Updatable<Spliterator<EvolutionResult<G, C>>>>
	_updatables = new CopyOnWriteArrayList<>();

	private final AtomicReference<EvolutionStreamable<G, C>>
	_engine = new AtomicReference<>();

	public UpdatableEngine(final EvolutionStreamable<G, C> engine) {
		_engine.set(requireNonNull(engine));
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		/*
		final UpdatableSpliterator<G, C> spliterator =
			new UpdatableSpliterator<>(this, _engine.get().stream(start).spliterator());

		return new EvolutionStreamImpl<G, C>(spliterator, false);
		 */
		return null;
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		/*
		final UpdatableSpliterator<G, C> spliterator =
			new UpdatableSpliterator<>(this, _engine.get().stream(init).spliterator());

		return new EvolutionStreamImpl<G, C>(spliterator, false);
		 */
		return null;
	}

	@Override
	public void update(final EvolutionStreamable<G, C> engine) {
		for (Updatable<Spliterator<EvolutionResult<G, C>>> updatable : _updatables) {
			updatable.update(engine.stream().spliterator());
		}
	}

	void addUpdatable(final Updatable<Spliterator<EvolutionResult<G, C>>> updatable) {

	}

	void removeUpdatable(final Updatable<Spliterator<EvolutionResult<G, C>>> updatable) {

	}

}
