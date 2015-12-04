/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObject.java 265 2011-08-04 09:45:18Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkObject.java $
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;

/**
 * Basic implementation of hashable objects in ELK, typically syntactic
 * structures like axioms or class expressions. ElkObjects are immutable.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public abstract class ElkObjectImpl implements ElkObject {

	/**
	 * hash code, computed on demand
	 */
	private int hashCode_ = 0;

	@Override
	public int hashCode() {
		if (hashCode_ == 0) {
			hashCode_ = ElkObjectHash.hashCode(this);
		}
		// else
		return hashCode_;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o || hashCode() == o.hashCode()) {
			return true;
		}	
		// else
		return ElkObjectEquality.equals(this, o);
	}

	@Override
	public String toString() {
		return OwlFunctionalStylePrinter.toString(this);
	}

}
