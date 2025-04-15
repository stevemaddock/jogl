[back](../README.md)

# Chapter 2. Rendering triangles

Please read [Joey's Hello Triangle example](https://learnopengl.com/Getting-started/Hello-Triangle) before continuing.

You will need to install JOGL before you can do any practical work. Appendix A 'Installing JOGL' describes how to do this.

This chapter is composed of three main sections:

1. Creating a drawing surface
2. Drawing a triangle
3. Element Buffer Objects

Section 2.1 is specific to Java and requires some knowledge of how to build an interface using the Java Swing classes. If you have not used these before, it would be useful to work through [Oracle's Java tutorial on Swing](https://docs.oracle.com/javase/tutorial/uiswing/index.html).

<figure>
  <img src="/ch2/img/A02_output.png" alt="output from A02" width="100" align="center">
  <figcaption><strong>Figure 2.1.</strong> A single triangle</figcaption>
</figure>

<p></p>

[This is a comment that will be hidden.]: # 

Section 2.2 focuses on how to draw a single 2D triangle. The aim is to first use a simple triangle to understand all the 'housekeeping' that comes with writing an OpenGL program. We will move on to 3D shapes in a later chapter. Before, you attempt this section, please read [Joey's Hello Triangle example] (https://learnopengl.com/Getting-started/Hello-Triangle). Initially, the focus will be on using a single colour for the triangle (Figure 2.1). Later we will look at multiple colours. We will refer to this process as 'rendering' the triangle and call the method that does the rendering 'render', accordingly. In later chapters, the term will have more meaning when we look at how to shade the surface of a triangle with respect to its relative position to a light source (or light sources). For now, we will ignore light sources and just shade the triangle a single colour.

Section 2.3 introduces Element Buffer Objects, which make it easier to manipulate and render multiple triangles. Figures 2.2 and 2.3 show two triangles used to make a rectangle. This could easily be replaced with many triangles.

# notes

- A01.java - open an empty window
- A02.java - draw a 2D triangle using vertex list <img src="/ch2/img/A02_output.png" alt="output from A02" width="100">
- A03.java - draw two 2D triangles using indices to the vertex list <img src="/ch2/img/A03_output.png" alt="output from A03" width="100">

[back](../README.md)
