package org.semanticweb.elk.reasoner.taxonomy.model;

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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * A {@link Node} created for an anonymous {@link ElkObject} that should not be
 * listed among its members
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 */
public class AnonymousNode<T extends ElkEntity> extends SimpleNode<T> implements
		Node<T> {

	public AnonymousNode(T anonymousMember, Iterable<T> allMembers,
			final ComparatorKeyProvider<ElkEntity> comparatorKeyProvider) {
		super(allMembers, comparatorKeyProvider);
		this.members.remove(anonymousMember);
	}

}
