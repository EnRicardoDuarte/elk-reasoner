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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class OWLProofUtils {

	public static boolean isAsserted(OWLExpression expression) {
		return expression.accept(new OWLExpressionVisitor<Boolean>() {

			@Override
			public Boolean visit(OWLAxiomExpression e) {
				return e.isAsserted();
			}

			@Override
			public Boolean visit(OWLLemmaExpression expression) {
				return false;
			}
			
		});
	}
	
	public static OWLAxiom getAxiom(OWLExpression expr) {
		return expr.accept(new OWLExpressionVisitor<OWLAxiom>() {

			@Override
			public OWLAxiom visit(OWLAxiomExpression e) {
				return e.getAxiom();
			}

			@Override
			public OWLAxiom visit(OWLLemmaExpression e) {
				return null;
			}

		});
	}
	
	public static OWLInferenceGraph computeInferenceGraph(OWLExpression proofRoot) throws ProofGenerationException {
		OWLInferenceGraph graph = new OWLInferenceGraph();
		Queue<OWLExpression> toDo = new LinkedList<OWLExpression>();
		Set<OWLExpression> done = new HashSet<OWLExpression>();
		
		toDo.add(proofRoot);
		done.add(proofRoot);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			for (OWLInference inf : next.getInferences()) {
				graph.addInference(inf);
				// Recursively unwind premise inferences
				//Set<OWLExpression> uniquePremises = new HashSet<OWLExpression>(inf.getPremises()); 
				
				for (OWLExpression premise : inf.getPremises()) {
					
					if (done.add(premise)) {
						toDo.add(premise);
					}
				}
			}
		}
		
		return graph;
	}
	
	public static String printProofTree(OWLExpression root) throws ProofGenerationException {
		/*class ExprIndent {
			private final OWLExpression expression;
			private final int indent;
			
			ExprIndent(OWLExpression e, int i) {
				expression = e;
				indent = i;
			}
		}
		
		StringBuilder builder = new StringBuilder();
		Queue<ExprIndent> toDo = new ArrayDeque<ExprIndent>();
		Set<OWLExpression> done = new HashSet<OWLExpression>();
		
		toDo.add(new ExprIndent(root, 0));
		
		for (;;) {
			ExprIndent next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			for (int i = 0; i < next.indent; i++) {
				builder.append("   ");
			}
			
			builder.append(next.expression).append('\n');
			
			if (!done.add(next.expression)) {
				continue;
			}
			
			for (OWLInference inf : next.expression.getInferences()) {
				for (int i = 0; i < next.indent + 1; i++) {
					builder.append("   ");
				}
				
				builder.append(inf.getName()).append('\n');
				
				for (OWLExpression premise : inf.getPremises()) {
					toDo.add(new ExprIndent(premise, next.indent + 2));
				}
			}
		}
		
		return builder.toString();*/
		
		StringBuilder builder = new StringBuilder();
		Set<OWLExpression> done = new HashSet<OWLExpression>();
		
		print(root, builder, done, 0);
		
		return builder.toString();
	}

	private static void print(OWLExpression expr, StringBuilder builder, Set<OWLExpression> done, int depth) throws ProofGenerationException {
		for (int i = 0; i < depth; i++) {
			builder.append("   ");
		}
		
		builder.append(expr).append('\n');
		
		if (!done.add(expr)) {
			return;
		}
		
		for (OWLInference inf : expr.getInferences()) {
			for (int i = 0; i < depth + 1; i++) {
				builder.append("   ");
			}
			
			builder.append(inf.getName()).append('\n');
			
			for (OWLExpression premise : inf.getPremises()) {
				print(premise, builder, done, depth + 2);	
			}
		}
	}
	
	public static void visitExpressionsInProofGraph(OWLExpression root, OWLExpressionVisitor<?> exprVisitor) throws ProofGenerationException {
		Queue<OWLExpression> toDo = new LinkedList<OWLExpression>();
		Set<OWLExpression> done = new HashSet<OWLExpression>();
		
		toDo.add(root);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (done.add(next)) {
				next.accept(exprVisitor);
				
				for (OWLInference inf : next.getInferences()) {
					//FIXME
					//System.err.println(inf);
					
					for (OWLExpression premise : inf.getPremises()) {
						toDo.add(premise);
					}
				}
			}
		}
	}
}