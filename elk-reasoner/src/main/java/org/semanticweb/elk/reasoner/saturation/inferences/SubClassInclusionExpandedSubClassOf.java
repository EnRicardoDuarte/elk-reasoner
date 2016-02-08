/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * A {@link SubClassInclusionDecomposed} obtained from a
 * {@link SubClassInclusionComposed} by unfolding a super-class under an
 * {@link IndexedSubClassOfAxiom}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class SubClassInclusionExpandedSubClassOf
		extends
			AbstractSubClassInclusionDecomposedInference {

	private final IndexedClassExpression premiseSubsumer_;

	private final ElkAxiom reason_;

	public SubClassInclusionExpandedSubClassOf(IndexedContextRoot inferenceRoot,
			IndexedClassExpression premiseSubsumer,
			IndexedClassExpression conclusion, ElkAxiom reason) {
		super(inferenceRoot, conclusion);
		this.premiseSubsumer_ = premiseSubsumer;
		this.reason_ = reason;
	}

	public IndexedClassExpression getPremiseSubsumer() {
		return this.premiseSubsumer_;
	}

	public ElkAxiom getReason() {
		return reason_;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public SubClassInclusionComposed getFirstPremise() {
		return FACTORY.getSubClassInclusionComposed(getOrigin(),
				premiseSubsumer_);
	}

	public IndexedSubClassOfAxiom getSecondPremise() {
		return FACTORY.getIndexedSubClassOfAxiom(reason_, premiseSubsumer_,
				getSubsumer());
	}

	@Override
	public String toString() {
		return super.toString() + " (subclassof)";
	}

	@Override
	public final <O> O accept(
			SubClassInclusionDecomposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(SubClassInclusionExpandedSubClassOf inference);
		
	}
	
}