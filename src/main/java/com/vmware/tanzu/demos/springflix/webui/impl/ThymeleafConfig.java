/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
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

package com.vmware.tanzu.demos.springflix.webui.impl;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.aot.hint.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(ThymeleafConfig.class)
class ThymeleafConfig implements RuntimeHintsRegistrar {
    @Bean
    LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        try {
            hints.reflection().registerType(TypeReference.class.forName("java.util.ImmutableCollections$ListN", false, classLoader), MemberCategory.INVOKE_PUBLIC_METHODS);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(ClassUtils.forName("java.util.List", classLoader), "isEmpty"), ExecutableMode.INVOKE);
            hints.reflection().registerMethod(ReflectionUtils.findMethod(String.class, "equals", Object.class), ExecutableMode.INVOKE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to register native hints for Thymeleaf", e);
        }
    }
}
