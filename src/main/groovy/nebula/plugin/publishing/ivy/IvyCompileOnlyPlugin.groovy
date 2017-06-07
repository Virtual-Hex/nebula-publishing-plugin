/*
 * Copyright 2017 Netflix, Inc.
 *
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
package nebula.plugin.publishing.ivy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.publish.ivy.IvyPublication

class IvyCompileOnlyPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply IvyBasePublishPlugin

        project.publishing {
            publications {
                withType(IvyPublication) {
                    descriptor.withXml { XmlProvider xml ->
                        project.plugins.withType(JavaBasePlugin) {
                            def root = xml.asNode()
                            def dependencies = project.configurations.compileOnly.dependencies
                            if (dependencies.size() > 0) {
                                def confs = root.configurations ? root.configurations[0] : root.appendNode('configurations')
                                confs.appendNode('conf', [name: 'provided', visibility: 'public'])
                                def deps = root.dependencies ? root.dependencies[0] : root.appendNode('dependencies')
                                dependencies.each { dep ->
                                    def newDep = deps.appendNode('dependency')
                                    newDep.@org = dep.group
                                    newDep.@name = dep.name
                                    newDep.@rev = dep.version
                                    newDep.@conf = 'provided'
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
