package org.karina.lang.compiler.model;

import com.google.common.collect.ImmutableList;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.objects.KType;

import java.util.Objects;

public record NameAndSignature(String name, Signature signature) { }
