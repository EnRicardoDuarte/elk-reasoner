package org.semanticweb.elk.reasoner.saturation.conclusions.model;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
 * %%
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
 * #L%
 */

import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;

/**
 * A {@link ObjectPropertyConclusion} representing a derived sub object property
 * axiom with sub-property expression represented by {@link #getSubChain()} and
 * super-property expression represented by {@link #getSuperChain()}. For
 * example, a {@link SubPropertyChain} with {@link #getSubChain()} =
 * {@code ObjectPropertyChain(:r :s)} and {@link #getSuperChain()} = {@code :h}
 * represents {@code SubObjectProperyOf(ObjectPropertyChain(:r :s) :h)}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public interface SubPropertyChain extends ObjectPropertyConclusion {

	/**
	 * @return the {@code IndexedPropertyChain} corresponding to the
	 *         sub-property of the {@link ElkSubObjectPropertyOfAxiom}
	 *         represented by this {@link SubPropertyChain}
	 * 
	 * @see ElkSubObjectPropertyOfAxiom#getSubObjectPropertyExpression()
	 */
	public IndexedPropertyChain getSubChain();

	/**
	 * @return the {@code IndexedPropertyChain} corresponding to the
	 *         super-property of the {@link ElkSubObjectPropertyOfAxiom}
	 *         represented by this {@link SubPropertyChain}
	 * 
	 * @see ElkSubObjectPropertyOfAxiom#getSuperObjectPropertyExpression()
	 */
	public IndexedPropertyChain getSuperChain();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		SubPropertyChain getSubPropertyChain(IndexedPropertyChain subChain,
				IndexedPropertyChain superChain);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		public O visit(SubPropertyChain conclusion);

	}

}