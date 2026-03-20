(
SynthDef.new(name:\marimba, ugenGraphFunc:{
    | out, freq = 440, pan = 0, amp = 1, atk = 0.01, sus = 0.01, rel = 1.5 |
    var mod = SinOsc.kr(880, mul: Line.kr(100,0,atk * 3));
    var source = Pulse.ar(freq + mod, mul: amp) * Env.linen(atk,sus,rel, curve: -2).ar(doneAction: Done.freeSelf);
    var filtered = LPF.ar(
        source,
        Env.perc(0.01,0.2).kr() * (freq) + (freq / 2)
    );
    var panned = Pan2.ar(filtered, pan);
    Out.ar(bus:out, channelsArray:panned)
}, rates:nil, prependArgs:nil, variants:nil, metadata:nil).add;
)

Tdef(\example2, {
    var counter = 0;
    var delta;
    loop {
        Synth(\marimba);
        delta = sin(counter) + 1.01;
        delta.postln;
        (1).yield;
    }
});

Tdef(\example2).play;
