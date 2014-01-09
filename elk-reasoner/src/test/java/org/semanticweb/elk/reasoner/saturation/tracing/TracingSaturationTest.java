/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingSaturationTest {
	
	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TracingSaturationTest.class);
	
	/*
	 * Runs reasoning with a post-processing stage that checks that traces of all conclusions have been fully computed.
	 * 
	 */
	@Test
	public void testBasicTracing() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"classification_test_input/PropertyChains.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass d = factory.getClass(new ElkFullIri("http://example.org/D"));
			
			reasoner.explainSubsumption(a, d, new BaseTracedConclusionVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.RECURSIVE);
			

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testDuplicateInferenceOfConjunction() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/DuplicateConjunction.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			

			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression bAndC = factory.getObjectIntersectionOf(b,  c);
			final AtomicInteger inferenceCounter = new AtomicInteger(0);
			
			reasoner.explainSubsumption(a, bAndC, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					inferenceCounter.incrementAndGet();
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.NON_RECURSIVE);
			
			assertEquals("Must be precisely one inference for " + bAndC, 1, inferenceCounter.get());
			

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testDuplicateInferenceOfExistential() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/DuplicateExistential.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
			final AtomicInteger inferenceCounter = new AtomicInteger(0);
			
			TracedConclusionVisitor<Void, Void> aTraceChecker = new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					inferenceCounter.incrementAndGet();
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			};
			
			reasoner.explainSubsumption(a, rSomeC, aTraceChecker, TRACE_MODE.NON_RECURSIVE);
			assertEquals("Must be precisely one inference for " + rSomeC, 1, inferenceCounter.get());
			
			reasoner.explainSubsumption(b, c, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.NON_RECURSIVE);
			// now check that we didn't get a duplicate inference in A due to tracing B
			inferenceCounter.set(0);
			
			reasoner.explainSubsumption(a, rSomeC, aTraceChecker, TRACE_MODE.NO_TRACING);
			assertEquals("Must be precisely one inference for " + rSomeC, 1, inferenceCounter.get());

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testDontTraceOtherContexts() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/DuplicateExistential.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);			
			
			reasoner.explainSubsumption(a, rSomeC, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.NON_RECURSIVE);
			
			reasoner.explainSubsumption(b, b, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					fail("This context should not be traced");
					return null;
				}
				
			}, TRACE_MODE.NO_TRACING/*do not trace B, we're checking if it's been unintentionally traced when we non-recursively traced A*/);

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testDuplicateInferenceViaComposition() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/DuplicateComposition.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
			final AtomicInteger inferenceCounter = new AtomicInteger(0);
			
			reasoner.explainSubsumption(a, rSomeC, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					inferenceCounter.incrementAndGet();
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.RECURSIVE);
			
			final AtomicBoolean bTraced = new AtomicBoolean(false);
			
			reasoner.explainSubsumption(b, b, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					bTraced.set(true);
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.NO_TRACING);

			assertTrue("B must be traced recursively", bTraced.get());
			assertEquals("Must be precisely one inference for " + rSomeC, 1, inferenceCounter.get());

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testDuplicateInferenceOfReflexiveExistential() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/DuplicateReflexiveExistential.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
			final AtomicInteger inferenceCounter = new AtomicInteger(0);
			
			reasoner.explainSubsumption(a, rSomeC, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					inferenceCounter.incrementAndGet();
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.NON_RECURSIVE);
			
			assertEquals("Must be precisely one inference for " + rSomeC, 1, inferenceCounter.get());

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testRecursiveTracingExistential() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/RecursiveExistential.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
			
			final AtomicInteger aImpliesRSomeC = new AtomicInteger(0);
			final AtomicInteger bImpliesC = new AtomicInteger(0);
			
			reasoner.explainSubsumption(a, rSomeC, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					aImpliesRSomeC.incrementAndGet();
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.RECURSIVE);
			
			assertEquals("A => R some C must be derived once", 1, aImpliesRSomeC.get());
			
			reasoner.explainSubsumption(b, c, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					bImpliesC.incrementAndGet();
					
					return null;
				}
				
			}, TRACE_MODE.NO_TRACING);
			
			assertEquals("B => C must be derived once when A is traced", 1, bImpliesC.get());

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	@Test
	public void testRecursiveTracingComposition() throws ElkException, IOException {
		InputStream stream = null;
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					"tracing/RecursiveComposition.owl");

			List<ElkAxiom> ontology = loadAxioms(stream);
			TestChangesLoader initialLoader = new TestChangesLoader();			
			ReasonerStageExecutor executor = new LoggingStageExecutor();
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader, executor);

			for (ElkAxiom axiom : ontology) {
				initialLoader.add(axiom);
			}

			reasoner.getTaxonomy();
			
			ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
			ElkClass b = factory.getClass(new ElkFullIri("http://example.org/B"));
			ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
			ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
			ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
			final AtomicInteger aImpliesRSomeC = new AtomicInteger(0);
			final AtomicInteger bImpliesRSomeC = new AtomicInteger(0);
			
			reasoner.explainSubsumption(a, rSomeC, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					aImpliesRSomeC.incrementAndGet();
					
					return super.defaultTracedVisit(conclusion, v);
				}
				
			}, TRACE_MODE.RECURSIVE);
			
			reasoner.explainSubsumption(b, b, new BaseTracedConclusionVisitor<Void, Void>() {
				
				@Override
				protected Void defaultTracedVisit(TracedConclusion conclusion, Void v) {
					LOGGER_.trace("traced inference: {}", InferencePrinter.print(conclusion));
					
					bImpliesRSomeC.incrementAndGet();
					
					return null;
				}
				
			}, TRACE_MODE.NO_TRACING);
			
			assertEquals("Must be precisely one inference for A => R some C", 1, aImpliesRSomeC.get());			
			assertEquals("Must be precisely one inference for B => B ", 1, bImpliesRSomeC.get());

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}	
	

	private List<ElkAxiom> loadAxioms(InputStream stream) throws IOException,
			Owl2ParseException {
		return loadAxioms(new InputStreamReader(stream));
	}

	private List<ElkAxiom> loadAxioms(Reader reader) throws IOException,
			Owl2ParseException {
		Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(reader);
		final List<ElkAxiom> axioms = new ArrayList<ElkAxiom>();

		parser.accept(new Owl2ParserAxiomProcessor() {

			@Override
			public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
			}

			@Override
			public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				axioms.add(elkAxiom);
			}

			@Override
			public void finish() throws Owl2ParseException {
				// everything is processed immediately
			}
		});

		return axioms;
	}
}