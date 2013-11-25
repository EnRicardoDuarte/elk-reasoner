/**
 * 
 */
package org.semanticweb.elk.owl.implementation.literals;
/*
 * #%L
 * ELK OWL Model Implementation
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

import org.semanticweb.elk.owl.interfaces.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkNmTokenLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkNmTokenLiteralImpl extends ElkTokenLiteralImpl implements
		ElkNmTokenLiteral {

	
	public ElkNmTokenLiteralImpl(String lexicalForm) {
		super(lexicalForm);
	}
	
	@Override
	public NmTokenDatatype getDatatype() {
		return ElkDatatypeMap.XSD_NMTOKEN;
	}

	@Override
	public <O> O accept(ElkLiteralVisitor<O> visitor) {
		return visitor.visit(this);
	}
}