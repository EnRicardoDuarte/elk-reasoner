package org.semanticweb.elk.reasoner.saturation.inferences;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * 
 * A {@link SubClassInclusionDecomposed} in which the super-class is 
 * obtained from the second conjunct of the premise.
 * 
 * @see IndexedObjectIntersectionOf#getSecondConjunct()
 * 
 * @author "Yevgeny Kazakov"
 *  
 */
public class SubClassInclusionDecomposedSecondConjunct extends SubClassInclusionDecomposedConjunct {

	public SubClassInclusionDecomposedSecondConjunct(IndexedContextRoot root,
			IndexedObjectIntersectionOf subsumer) {
		super(root, subsumer, subsumer.getSecondConjunct());
	}

	@Override
	public final <O> O accept(SubClassInclusionDecomposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(SubClassInclusionDecomposedSecondConjunct inference);
		
	}

}
