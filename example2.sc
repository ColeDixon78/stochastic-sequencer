(
SynthDef.new(name:\marimba, ugenGraphFunc:{
    | out, freq = 440, pan = 0, amp = 1, atk = 0.1, sus = 0.01, rel = 0.5 |
    var source = SinOsc.ar(freq, amp) * Env.linen(atk,sus,rel).ar(doneAction: Done.freeSelf);
    var panned = Pan2.ar(source, pan);
    Out.ar(bus:out, channelsArray:panned)
}, rates:nil, prependArgs:nil, variants:nil, metadata:nil).add;
)

Tdef(\example2, {
    var delta = 2;
    loop {
        Synth(\marimba);
        delta.yield;
    }
});

Tdef(\example2).play;

Server.default.waitForBoot
