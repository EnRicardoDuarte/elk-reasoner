/**
 * 
 */
package org.semanticweb.owlapitools.proofs.util;
/*
 * #%L
 * OWL API Proofs Model
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionWrap;

/**
 * Generic base class for {@link OWLExpression}s which transform their
 * {@link OWLInference}s before returning them from the method
 * {@link #getInferences()}. The transformation is done using the provided
 * instance of {@link Operations.Transformation}. A special case of
 * transformation is filtering, i.e., eliminating some inferences from the
 * output.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public abstract class TransformedOWLExpression<E extends OWLExpression, T extends OWLInferenceTransformation> implements OWLExpression, OWLExpressionWrap {

	protected final E expression;
	
	protected final T transformation; 
	
	protected final Operations.Transformation<OWLInference, TransformedOWLInference<T>> propagation = new Operations.Transformation<OWLInference, TransformedOWLInference<T>>() {

		@Override
		public TransformedOWLInference<T> transform(OWLInference inf) {
			return propagateTransformation(inf);
		}}; 
	
	public TransformedOWLExpression(E expr, T f) {
		expression = expr;
		transformation = f;
	}
	
	@Override
	public Iterable<TransformedOWLInference<T>> getInferences() throws ProofGenerationException {
		return Operations.mapConcat(expression.getInferences(), new Operations.Transformation<OWLInference, Iterable<TransformedOWLInference<T>>>() {

			@Override
			public Iterable<TransformedOWLInference<T>> transform(OWLInference inf) {
				Iterable<OWLInference> transformed = transformation.transform(inf); 
				
				return Operations.map(transformed, propagation);
			}
			
		});
	}

	protected TransformedOWLInference<T> propagateTransformation(OWLInference inf) {
		return new TransformedOWLInference<T>(inf, transformation);
	}
	
	public T getFilterCondition() {
		return transformation;
	}

	@Override
	public String toString() {
		return expression.toString();
	}
	
	@Override
	public E getExpression() {
		return expression;
	}

	@Override
	public boolean equals(Object obj) {
		return expression.equals(obj);
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}
	
}