/*
 * Copyright 2019 the original author or authors.
 *
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
 */
package org.springframework.cloud.cnb.jdbc;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.cnb.Binding;


public class PostgresJdbcKind implements JdbcKind{

    public static final List<String> POSTGRES_KINDS = Arrays.asList("postgres", "postgresql");
    public static final String POSTGRES_SCHEME = "postgres";

    @Override
    public boolean forBinding(Binding binding) {
        return POSTGRES_KINDS.contains(binding.getKind());
    }

    @Override
    public String getScheme() {
        return POSTGRES_SCHEME;
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }
}