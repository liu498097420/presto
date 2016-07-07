/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.presto.sql.planner.assertions;

import com.facebook.presto.Session;
import com.facebook.presto.metadata.Metadata;
import com.facebook.presto.sql.planner.plan.JoinNode;
import com.facebook.presto.sql.planner.plan.PlanNode;
import com.google.common.base.MoreObjects;

import java.util.List;

import static java.util.Objects.requireNonNull;

final class JoinMatcher
        implements Matcher
{
    private final List<AliasPair> equiCriteria;

    JoinMatcher(List<AliasPair> equiCriteria)
    {
        this.equiCriteria = requireNonNull(equiCriteria, "equiCriteria is null");
    }

    @Override
    public boolean matches(PlanNode node, Session session, Metadata metadata, SymbolAliases symbolAliases)
    {
        if (node instanceof JoinNode) {
            JoinNode joinNode = (JoinNode) node;
            if (joinNode.getCriteria().size() == equiCriteria.size()) {
                int i = 0;
                for (JoinNode.EquiJoinClause equiJoinClause : joinNode.getCriteria()) {
                    AliasPair expectedEquiClause = equiCriteria.get(i++);
                    symbolAliases.put(expectedEquiClause.left, equiJoinClause.getLeft());
                    symbolAliases.put(expectedEquiClause.right, equiJoinClause.getRight());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("equiCriteria", equiCriteria)
                .toString();
    }
}
