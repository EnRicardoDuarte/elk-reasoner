/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.OwlApiReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseClassExpressionQueryTest;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class OwlApiClassExpressionEquivalentClassesQueryTest extends
		BaseClassExpressionQueryTest<OWLClassExpression, EquivalentEntitiesTestOutput<OWLClass>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			"Inconsistent.owl",// Throwing InconsistentOntologyException
			"InconsistentInstances.owl",// Throwing InconsistentOntologyException
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final TestInput input) {
		return Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

	public OwlApiClassExpressionEquivalentClassesQueryTest(
			final TestManifestWithOutput<ClassQueryTestInput<OWLClassExpression>, EquivalentEntitiesTestOutput<OWLClass>, EquivalentEntitiesTestOutput<OWLClass>> manifest) {
		super(manifest,
				new OwlApiReasoningTestDelegate<EquivalentEntitiesTestOutput<OWLClass>>(
						manifest) {

					@Override
					public EquivalentEntitiesTestOutput<OWLClass> getActualOutput()
							throws Exception {
						final Node<OWLClass> equivalent = getReasoner()
								.getEquivalentClasses(
										manifest.getInput().getClassQuery());
						return new OwlApiEquivalentEntitiesTestOutput(
								equivalent);
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ReasonerInterruptedException.class;
					}

				});
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, BaseClassExpressionQueryTest.class, "owl",
				"expected",
				new MultiManifestCreator<ClassQueryTestInput<OWLClassExpression>, EquivalentEntitiesTestOutput<OWLClass>, EquivalentEntitiesTestOutput<OWLClass>>() {

					@Override
					public Collection<? extends TestManifestWithOutput<ClassQueryTestInput<OWLClassExpression>, EquivalentEntitiesTestOutput<OWLClass>, EquivalentEntitiesTestOutput<OWLClass>>> createManifests(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();

							return OwlExpectedTestOutputLoader.load(outputIS)
									.getEquivalentEntitiesManifests(input);

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

}
