package org.karina.lang.compiler.model;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.objects.KType;

import java.util.List;
import java.util.Objects;

public record Signature(ImmutableList<KType> parameters, KType returnType) { }
