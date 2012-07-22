/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.informantproject.core.weaving;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class BasicMisc implements Misc, Misc2, Misc3 {

    // Misc implementation
    public void execute1() {
        // do some stuff that can be intercepted
        new BasicMisc();
        withInnerArg(null);
    }

    public String executeWithReturn() {
        return "xyz";
    }

    public void executeWithArgs(String one, int two) {}

    // Misc2 implementation
    public void execute2() {}

    // Misc3 implementation
    public BasicMisc identity(BasicMisc misc) {
        return misc;
    }

    private void withInnerArg(@SuppressWarnings("unused") Inner inner) {}

    public static class Inner {}
}