/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Aug 8, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.al.nodes.score.density.nodepotential;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knime.base.util.kdtree.NearestNeighbour;
import org.knime.core.data.RowKey;
import org.mockito.junit.MockitoJUnitRunner;

import gnu.trove.list.TDoubleList;

/**
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
@RunWith(MockitoJUnitRunner.class)
public class PotentialDataPointTest {

    private static PotentialDataPoint create(final String key, final double... values) {
        return new PotentialDataPoint(new RowKey(key), values);
    }

    private PotentialDataPoint m_testInstance;

    @Before
    public void init() {
        m_testInstance = create("key", 1, 1);
    }

    @Test
    public void testRegisterNeighbor() throws Exception {
        PotentialDataPoint n1 = create("n1", 0, 0);
        PotentialDataPoint n2 = create("n2", 1, 0);
        final NearestNeighbour<PotentialDataPoint> mock = mock(NearestNeighbour.class);
        when(mock.getData()).thenReturn(n1, n2);
        when(mock.getDistance()).thenReturn(2.0, 1.0);
        m_testInstance.registerNeighbor(mock);
        m_testInstance.registerNeighbor(mock);
        Collection<PotentialDataPoint> neighbors = m_testInstance.getNeighbors();
        TDoubleList distances = m_testInstance.getSquaredDistances();
        assertEquals(2, neighbors.size());
        Iterator<PotentialDataPoint> iter = neighbors.iterator();
        assertEquals(n1, iter.next());
        assertEquals(n2, iter.next());
        assertEquals(4.0, distances.get(0), 1e-6);
        assertEquals(1.0, distances.get(1), 1e-6);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterNeighborFailsOnSelfArgument() throws Exception {
        final NearestNeighbour<PotentialDataPoint> mock = mock(NearestNeighbour.class);
        when(mock.getData()).thenReturn(m_testInstance);
        m_testInstance.registerNeighbor(mock);
    }

    @Test
    public void testNormalizeDensity() throws Exception {
        m_testInstance.setDensity(10);
        assertEquals(10, m_testInstance.getDensity(), 1e-6);
        m_testInstance.normalizeDensity();
        assertEquals(10, m_testInstance.getDensity(), 1e-6);
    }
}
