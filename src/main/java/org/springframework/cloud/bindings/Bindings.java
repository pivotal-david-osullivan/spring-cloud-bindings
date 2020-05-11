/*
 * Copyright 2020 the original author or authors.
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
package org.springframework.cloud.bindings;

import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A representation of a collection of bindings as defined by the
 * <a href="https://github.com/buildpacks/spec/blob/master/extensions/bindings.md">Cloud Native Buildpacks Specification</a>.
 */
public final class Bindings {

    /**
     * The name of the environment variable to read to determine the bindings file system root.  Specified by the Cloud
     * Native Buildpacks Specification.
     */
    public static final String CNB_BINDINGS = "CNB_BINDINGS";

    private final List<Binding> bindings;

    /**
     * Creates a new {@code Bindings} instance, using the {@code $CNB_BINDINGS} environment variable to determine the
     * file system root.  If the {@code $CNB_BINDINGS} environment variable is not set, an empty {@code Bindings} is
     * returned. If the directory does not exist, an empty {@code Bindings} is returned.
     */
    public Bindings() {
        this(System.getenv(CNB_BINDINGS));
    }

    /**
     * Creates a new {@code Bindings} instance, using the specified {@code path}.  If the directory does not exist, an
     * empty {@code Bindings} is returned.
     *
     * @param path the path to populate the {@code Bindings} from.
     */
    public Bindings(@Nullable String path) {
        if (path == null) {
            this.bindings = Collections.emptyList();
            return;
        }

        Path p = Paths.get(path);

        if (!Files.exists(p)) {
            this.bindings = Collections.emptyList();
            return;
        }

        if (!Files.isDirectory(p)) {
            throw new IllegalArgumentException(String.format("%s is not a directory", p));
        }

        try {
            this.bindings = Files.list(p)
                    .map(Binding::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(String.format("unable to list children of '%s'", path), e);
        }
    }

    /**
     * Creates a new {@code Bindings} instance using the specified content.
     *
     * @param bindings the {@code Binding}s.
     */
    public Bindings(Binding... bindings) {
        this.bindings = Arrays.asList(bindings);
    }

    /**
     * Returns all the {@link Binding}s that were found during construction.
     */
    public List<Binding> getBindings() {
        return bindings;
    }

    /**
     * Returns a {@link Binding} with a given name.
     *
     * @param name the name of the {@code Binding} to find.
     * @return the {@code Binding} with a given name if it exists, {@code null} otherwise.
     */
    @Nullable
    public Binding findBinding(String name) {
        return bindings.stream()
                .filter(binding -> binding.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns zero or more {@link Binding}s with a given kind.  Equivalent to {@link #filterBindings(String, String)}.
     *
     * @param kind the kind of the {@code Binding} to find.
     * @return the collection of {@code Binding}s with a given kind.
     */
    public List<Binding> filterBindings(@Nullable String kind) {
        return filterBindings(kind, null);
    }

    /**
     * Return zero or more {@link Binding}s with a given kind and provider.  If {@code kind} or {@code provider} are
     * {@code null}, the result is not filtered on that argument.
     *
     * @param kind     the kind of {@code Binding} to find.
     * @param provider the provider of {@code Binding} to find
     * @return the collection of {@code Binding}s with a given kind and provider.
     */
    public List<Binding> filterBindings(@Nullable String kind, @Nullable String provider) {
        return bindings.stream()
                .filter(binding ->
                        (kind == null || binding.getKind().equalsIgnoreCase(kind)) &&
                                (provider == null) || binding.getProvider().equalsIgnoreCase(provider))
                .collect(Collectors.toList());
    }

}
