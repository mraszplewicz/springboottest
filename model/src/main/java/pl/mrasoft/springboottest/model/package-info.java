@GenericGenerator(
        name = "default",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
                @Parameter(name = "prefer_sequence_per_entity", value = "true"),
        }
)
package pl.mrasoft.springboottest.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;