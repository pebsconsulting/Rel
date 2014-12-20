package ca.mb.armchair.rel3.tests.engine;

import org.junit.Test;

import com.sleepycat.je.*;

import ca.mb.armchair.rel3.storage.*;
import ca.mb.armchair.rel3.storage.relvars.RelvarDefinition;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.storage.relvars.RelvarRealMetadata;
import ca.mb.armchair.rel3.types.Heading;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.generator.SelectAttributes;
import ca.mb.armchair.rel3.generator.References;
import ca.mb.armchair.rel3.interpreter.Instance;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.types.builtin.*;

public class TestStorage {
	
	private Instance instance;
	private RelDatabase database;
	
	public TestStorage() {
		instance = new Instance("./");
		database = instance.getDatabase();
	}
	
	@Test
	public void testStorage1() throws DatabaseException {
		
		Heading heading = new Heading();
		heading.add("x", TypeInteger.getInstance());
		heading.add("y", TypeRational.getInstance());
		heading.add("z", TypeCharacter.getInstance());
		
		SelectAttributes selection = new SelectAttributes();
		selection.add("x");		
		RelvarHeading headingDefinition = new RelvarHeading(heading);
		headingDefinition.addKey(selection);
		
		if (database.isRelvarExists("testvarx"))
			database.dropRelvar("testvarx");
		Generator generator = new Generator(database, System.out);
		database.createRealRelvar(generator, new RelvarDefinition("testvarx", new RelvarRealMetadata(database, headingDefinition, "User"), new References()));
		RelvarGlobal var = database.openGlobalRelvar("testvarx");
		
		System.out.println("Inserting tuples.");
		
		ValueRelationLiteral lit = new ValueRelationLiteral(generator);
		lit.insert(new ValueTuple(generator, new Value[] {ValueInteger.select(generator, 5), ValueRational.select(generator, 1.2), ValueCharacter.select(generator, "blah")}));
		lit.insert(new ValueTuple(generator, new Value[] {ValueInteger.select(generator, 6), ValueRational.select(generator, 2.3), ValueCharacter.select(generator, "blat")}));
		lit.insert(new ValueTuple(generator, new Value[] {ValueInteger.select(generator, 6), ValueRational.select(generator, 2.3), ValueCharacter.select(generator, "blat")}));
		lit.insert(new ValueTuple(generator, new Value[] {ValueInteger.select(generator, 7), ValueRational.select(generator, -2.2), ValueCharacter.select(generator, "zot")}));
		var.setValue(lit);
		 
		for (int i=8; i<1000; i++) {
			var.insert(generator, new ValueTuple(generator, new Value[] {ValueInteger.select(generator, i), ValueRational.select(generator, 1.2 * i), ValueCharacter.select(generator, "blah" + i)}));
		}

		System.out.println("Reading newly-inserted tuples.");
		
		TupleIterator ti = var.getValue(generator).iterator();
		try {
			while (ti.hasNext())
				System.out.println(ti.next());
		} finally {
			ti.close();
		}
		
		database.dropRelvar("testvarx");
	}
	
}